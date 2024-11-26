package ru.yogago.goyoga.data

data class SelectedIndexArray(
    val selectedIndex: Int,
    var arr: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SelectedIndexArray

        if (selectedIndex != other.selectedIndex) return false
        if (!arr.contentEquals(other.arr)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = selectedIndex
        result = 31 * result + (arr.contentHashCode())
        return result
    }
}