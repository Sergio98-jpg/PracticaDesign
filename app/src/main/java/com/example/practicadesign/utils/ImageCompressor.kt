package com.example.practicadesign.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utilidad para comprimir imágenes antes de subirlas al servidor.
 * 
 * Reduce el tamaño de las imágenes para ahorrar datos móviles y batería,
 * y hacer que la subida sea más rápida y confiable.
 * 
 * Configuración por defecto:
 * - Ancho máximo: 1920px
 * - Alto máximo: 1080px
 * - Calidad: 80%
 * - Tamaño máximo: 2MB
 */
object ImageCompressor {
    
    private const val TAG = "ImageCompressor"
    
    // Configuración de compresión
    private const val MAX_WIDTH = 1920
    private const val MAX_HEIGHT = 1080
    private const val COMPRESSION_QUALITY = 80 // 0-100
    private const val MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024 // 2MB
    
    /**
     * Comprime una imagen desde una URI y guarda el resultado en un archivo temporal.
     * 
     * @param context Contexto de la aplicación
     * @param sourceUri URI de la imagen original
     * @return URI del archivo comprimido, o null si falla la compresión
     */
    suspend fun compressImage(
        context: Context,
        sourceUri: Uri
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Iniciando compresión de imagen: $sourceUri")
            
            // 1. Leer la imagen original
            val inputStream = context.contentResolver.openInputStream(sourceUri)
                ?: return@withContext null.also {
                    Log.e(TAG, "No se pudo abrir el input stream para: $sourceUri")
                }
            
            val originalBitmap = try {
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                Log.e(TAG, "Error al decodificar bitmap", e)
                null
            } finally {
                inputStream.close()
            }
            
            if (originalBitmap == null) {
                Log.e(TAG, "No se pudo decodificar el bitmap desde: $sourceUri. Posible formato no soportado o archivo corrupto.")
                return@withContext null
            }
            
            Log.d(TAG, "Imagen original: ${originalBitmap.width}x${originalBitmap.height}, tamaño: ${originalBitmap.byteCount} bytes")
            
            // 2. Calcular el nuevo tamaño manteniendo el aspect ratio
            val (newWidth, newHeight) = calculateNewDimensions(
                originalBitmap.width,
                originalBitmap.height
            )
            val needsResize = newWidth != originalBitmap.width || newHeight != originalBitmap.height
            
            // 3. Obtener el bitmap de trabajo (redimensionado si es necesario)
            val workingBitmap = if (needsResize) {
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    newWidth,
                    newHeight,
                    true
                )
            } else {
                originalBitmap
            }
            
            // 4. Comprimir y guardar en un archivo temporal
            val compressedFile = createTempImageFile(context)
            val outputStream = FileOutputStream(compressedFile)
            
            // Comprimir con calidad especificada
            workingBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                COMPRESSION_QUALITY,
                outputStream
            )
            outputStream.flush()
            outputStream.close()
            
            // Liberar memoria del/los bitmaps si corresponde
            if (needsResize) {
                // Solo reciclar el redimensionado; el original se recicla después
                if (workingBitmap !== originalBitmap) {
                    workingBitmap.recycle()
                }
                // Reciclar original al final si es distinto al de trabajo
                if (!originalBitmap.isRecycled) {
                    originalBitmap.recycle()
                }
            } else {
                // No reciclar si es el mismo bitmap utilizado para comprimir
                // (lo gestionará el GC)
            }
            
            // 5. Verificar que el archivo comprimido no exceda el tamaño máximo
            val fileSize = compressedFile.length()
            Log.d(TAG, "Tamaño del archivo comprimido: $fileSize bytes")
            
            if (fileSize > MAX_FILE_SIZE_BYTES) {
                Log.w(TAG, "El archivo comprimido ($fileSize bytes) excede el tamaño máximo ($MAX_FILE_SIZE_BYTES bytes). Re-comprimiendo con menor calidad...")
                // Re-comprimir con menor calidad si es necesario
                return@withContext recompressWithLowerQuality(
                    context,
                    sourceUri,
                    compressedFile
                )
            }
            
            Log.d(TAG, "✓ Imagen comprimida exitosamente: ${compressedFile.length()} bytes")
            Uri.fromFile(compressedFile)
            
        } catch (e: IOException) {
            Log.e(TAG, "Error al comprimir imagen", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado al comprimir imagen", e)
            null
        }
    }
    
    /**
     * Calcula las nuevas dimensiones manteniendo el aspect ratio.
     * 
     * @param originalWidth Ancho original
     * @param originalHeight Alto original
     * @return Par de (nuevoAncho, nuevoAlto)
     */
    private fun calculateNewDimensions(
        originalWidth: Int,
        originalHeight: Int
    ): Pair<Int, Int> {
        // Si la imagen ya es más pequeña que el máximo, no redimensionar
        if (originalWidth <= MAX_WIDTH && originalHeight <= MAX_HEIGHT) {
            return Pair(originalWidth, originalHeight)
        }
        
        // Calcular el ratio de escalado
        val widthRatio = MAX_WIDTH.toFloat() / originalWidth
        val heightRatio = MAX_HEIGHT.toFloat() / originalHeight
        val ratio = minOf(widthRatio, heightRatio)
        
        // Aplicar el ratio manteniendo el aspect ratio
        val newWidth = (originalWidth * ratio).toInt()
        val newHeight = (originalHeight * ratio).toInt()
        
        return Pair(newWidth, newHeight)
    }
    
    /**
     * Crea un archivo temporal para guardar la imagen comprimida.
     * 
     * @param context Contexto de la aplicación
     * @return Archivo temporal
     */
    private fun createTempImageFile(context: Context): File {
        val cacheDir = context.cacheDir
        val timestamp = System.currentTimeMillis()
        return File(cacheDir, "compressed_image_$timestamp.jpg")
    }
    
    /**
     * Re-comprime una imagen con menor calidad si el archivo resultante es muy grande.
     * 
     * @param context Contexto de la aplicación
     * @param sourceUri URI de la imagen original
     * @param existingFile Archivo que ya fue comprimido pero es muy grande
     * @return URI del archivo re-comprimido, o null si falla
     */
    private suspend fun recompressWithLowerQuality(
        context: Context,
        sourceUri: Uri,
        existingFile: File
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            // Eliminar el archivo anterior
            existingFile.delete()
            
            // Leer la imagen original nuevamente
            val inputStream = context.contentResolver.openInputStream(sourceUri)
                ?: return@withContext null
            
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            if (originalBitmap == null) {
                return@withContext null
            }
            
            // Calcular dimensiones y determinar si hay que redimensionar
            val (newWidth, newHeight) = calculateNewDimensions(
                originalBitmap.width,
                originalBitmap.height
            )
            val needsResize = newWidth != originalBitmap.width || newHeight != originalBitmap.height
            
            // Obtener bitmap de trabajo
            val workingBitmap = if (needsResize) {
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    newWidth,
                    newHeight,
                    true
                )
            } else {
                originalBitmap
            }
            
            // Intentar diferentes niveles de calidad hasta que el archivo sea suficientemente pequeño
            var quality = COMPRESSION_QUALITY - 10
            var compressedFile: File? = null
            
            while (quality >= 50 && compressedFile == null) {
                val tempFile = createTempImageFile(context)
                val outputStream = FileOutputStream(tempFile)
                
                workingBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    quality,
                    outputStream
                )
                outputStream.flush()
                outputStream.close()
                
                if (tempFile.length() <= MAX_FILE_SIZE_BYTES) {
                    compressedFile = tempFile
                } else {
                    tempFile.delete()
                    quality -= 10
                }
            }
            
            // Liberar memoria del/los bitmaps si corresponde
            if (needsResize) {
                if (workingBitmap !== originalBitmap && !workingBitmap.isRecycled) {
                    workingBitmap.recycle()
                }
                if (!originalBitmap.isRecycled) {
                    originalBitmap.recycle()
                }
            }
            
            if (compressedFile != null) {
                Log.d(TAG, "Imagen re-comprimida exitosamente con calidad $quality: ${compressedFile.length()} bytes")
                Uri.fromFile(compressedFile)
            } else {
                Log.e(TAG, "No se pudo comprimir la imagen a un tamaño aceptable")
                null
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al re-comprimir imagen", e)
            null
        }
    }
    
    /**
     * Limpia archivos temporales de imágenes comprimidas.
     * 
     * Debe ser llamado periódicamente para liberar espacio en el cache.
     * 
     * @param context Contexto de la aplicación
     */
    fun clearTempFiles(context: Context) {
        try {
            val cacheDir = context.cacheDir
            cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("compressed_image_") && file.name.endsWith(".jpg")) {
                    // Eliminar archivos más antiguos de 24 horas
                    val fileAge = System.currentTimeMillis() - file.lastModified()
                    val oneDayInMillis = 24 * 60 * 60 * 1000L
                    if (fileAge > oneDayInMillis) {
                        file.delete()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al limpiar archivos temporales", e)
        }
    }
}

