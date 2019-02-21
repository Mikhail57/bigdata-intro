import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer


fun main(args: Array<String>) {
    if (args.isEmpty() || args.size > 3) {
        error(
            "Usage: reverse <input file> <output file> [<mode>]\n" +
                    "Where <mode> is 'mem' or 'block'. It will be ignored, if no enough RAM available"
        )
    }

    var mode = -1

    if (args.size == 3) {
        mode = when (args[2].toLowerCase()) {
            "mem" -> 0
            "block" -> 1
            else -> -1
        }
    }

    val inputFile = File(args[0])
    val outputFile = File(args[1])

    if (!inputFile.isFile) {
        error("First parameter should be file and should exists")
    }

    if (outputFile.exists() && !outputFile.canWrite()) {
        error("Cannot write to ${outputFile.absoluteFile}")
    }

    val fileSize = inputFile.length()
    val availableRam = Runtime.getRuntime().freeMemory()

    if (fileSize * 2 < availableRam && mode == 0) {
        println("Copying in-memory...")
        copyFileInMemory(inputFile, outputFile)
    } else {
        println("Block copying...")
        val maxBlockSizeLong = availableRam / 2
        val maxBlockSize: Int = if (maxBlockSizeLong > Int.MAX_VALUE) Int.MAX_VALUE else maxBlockSizeLong.toInt()
        copyFileByBlocks(inputFile, outputFile, maxBlockSize)
    }
}

fun copyFileInMemory(inputFile: File, outputFile: File) {
    val fileInputStream = FileInputStream(inputFile)
    val fileOutputStream = FileOutputStream(outputFile)

    val allBytes = fileInputStream.readAllBytes()
    val reversed = allBytes.map { it.reverse() }.asReversed()

    fileOutputStream.write(reversed.toByteArray())
    fileOutputStream.close()
}

fun copyFileByBlocks(inputFile: File, outputFile: File, maxBlockSize: Int) {
    val reader = RandomAccessFile(inputFile, "r")
    val channel = reader.channel
    val fileOutputStream = FileOutputStream(outputFile)

    val inputFileSize = inputFile.length()
    val blockSize = maxBlockSize
    val blocksCount = Math.ceil(inputFileSize.toDouble() / maxBlockSize).toInt()


    val buff = ByteBuffer.allocate(blockSize)
    buff.limit(blockSize)

    for (i in (blocksCount - 1) downTo 0) {
        buff.clear()

        channel.position(i * blockSize.toLong())
        val bytesRead = channel.read(buff)
        buff.flip()

        var array = buff.array()
        if (blockSize != bytesRead) {
            array = buff.array().take(bytesRead).toByteArray()
        }

        val reversed = array.map { it.reverse() }.asReversed().toByteArray()

        fileOutputStream.write(reversed)
    }
    fileOutputStream.close()
}


fun Byte.reverse(): Byte {
    return Integer.reverse(this.toInt()).shr(24).toByte()
}