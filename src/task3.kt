import java.io.*
import java.util.stream.Collectors


fun main(args: Array<String>) {
    if (args.size !in 1..2) {
        error(
            "Usage: counter <file-name> [<count>]\n" +
                    "Where <count> is the number of words to be used in popularity list"
        )
    }

    val file = File(args[0])
    if (!file.isFile) {
        error("First parameter should be file and should exists")
    }

    val count = if (args.size == 2) args[1].toInt() else 3
    if (count <= 0) {
        error("Count should be greater than 0")
    }

    val fileReader = BufferedReader(FileReader(file))

    val regex = Regex("[\\s.,!?]")

    val counts = fileReader.lines().parallel().flatMap { line ->
        line.toLowerCase().split(regex).stream().filter { word -> word.length >= 4 }
    }.collect(Collectors.toConcurrentMap<String, String, Int>({ it }, { 1 }, Integer::sum))

    val popularWords = counts.entries.sortedByDescending { it.value }.take(count)

    popularWords.forEach {
        val (word, wordCount) = it
        println("$word\t-\t$wordCount")
    }
}