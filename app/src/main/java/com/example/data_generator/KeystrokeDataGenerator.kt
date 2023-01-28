package com.example.data_generator

class KeystrokeDataGenerator(private val activity: KeystrokeCallback) {
    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isEmpty()) {
            return
        }

        val latestCharacter = s[start + count - 1]
        val output = generateOutput(latestCharacter)

        activity.updateActivityData(output)
    }

    private fun generateOutput(text: Char?): String {
        val ascii = text!!.code
        val time = (System.currentTimeMillis() % 1000)
        return (time.toInt().toChar() + ascii).toString()
    }
}