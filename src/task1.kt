import java.util.*
import java.util.stream.IntStream
import kotlin.collections.ArrayList


fun main() {
    val scanner = Scanner(System.`in`)

    print("Start: ")
    val start = scanner.nextInt()
    print("End: ")
    val end = scanner.nextInt()

    if (end <= start) {
        error("End number should be greater than start number")
    }

    val invertPrimesBitSet = BitSet(end)
    val primes = ArrayList<Int>()

    invertPrimesBitSet[0] = true
    invertPrimesBitSet[1] = true

    for (i in 2..end) {
        if (!invertPrimesBitSet[i]) {
            primes += i
            if (i * i <= end) {
                for (j in (i * i.toLong())..end step i.toLong()) {
                    invertPrimesBitSet[j.toInt()] = true
                }
            }
        }
    }

    val primesStream: IntStream = primes.stream().mapToInt(Int::toInt).parallel()

    val avg = primesStream.filter { it in (start + 1)..(end - 1) }.average().asDouble

    println("Average value of prime numbers between $start and $end equals $avg")

}