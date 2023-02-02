package com.example.moblpr

class PlateRecognizer {

    companion object {

        fun execute(value: String): String? {
            val values = value.split("\n")

            var plate: String? = null

            for (text: String in values) {
                var textValue = text.replace("[-\\s]".toRegex(), "")

                if (textValue.length == 7 && textValue.contains("\\d".toRegex())) {
                    val plateChars = textValue.toCharArray()

                    // se contains numero nas 3 primeiras posições da placa, tá errado, só pode letra
                    if (textValue.substring(0, 3).contains("\\d".toRegex())) {
                        for (i in 0..2) {
                            if (plateChars[i] == '0') {
                                plateChars[i] = 'O'
                            }
                            if (plateChars[i] == '1') {
                                plateChars[i] = 'I'
                            }
                        }
                    }

                    // se na quarta posição da placa for uma letra, ta errado, só pode numero
                    if (textValue.substring(3, 4).contains("[A-Z]".toRegex())) {
                        if (plateChars[3] == 'O' || plateChars[3] == 'Q') {
                            plateChars[3] = '0'
                        } else if (plateChars[3] == 'I') {
                            plateChars[3] = '1'
                        }
                    }

                    // se contains letra nas 2 últimas posições da placa, tá errado, só pode numero
                    if (textValue.substring(5).contains("\\D".toRegex())) {
                        for (i in 5..6) {
                            if (plateChars[i] == 'O' || plateChars[i] == 'Q') {
                                plateChars[i] = '0'
                            } else if (plateChars[i] == 'I') {
                                plateChars[i] = '1'
                            }
                        }
                    }

                    textValue = String(plateChars)

                    if (textValue.matches("^([A-Z]{3})(\\d)(\\d|[A-Z])(\\d{2})\$".toRegex())) {
                        plate = textValue
                        break
                    }
                }
            }

            return plate
        }

    }

}