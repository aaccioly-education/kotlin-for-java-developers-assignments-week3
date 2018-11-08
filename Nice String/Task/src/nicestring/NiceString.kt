package nicestring

fun String.isNice(): Boolean {

    return listOf(
            // bu, ba or be
            !contains(Regex("b[aeu]")),
            // at least three vowels
            Regex("[aeiou]").findAll(this).count() >= 3,
            // double letter
            contains(Regex("""([a-z])\1"""))).count { it } >= 2
}