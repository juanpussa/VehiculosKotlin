import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun testSerialPortCommunication(writePortName: String, readPortName: String) {
    val writePort = SerialPort.getCommPort(writePortName)
    val readPort = SerialPort.getCommPort(readPortName)

    writePort.baudRate = 9600
    readPort.baudRate = 9600

    if (!writePort.openPort()) {
        println("Error: Cannot open write port $writePortName")
        return
    }

    if (!readPort.openPort()) {
        println("Error: Cannot open read port $readPortName")
        writePort.closePort()
        return
    }

    // Coroutine to read data
    GlobalScope.launch {
        val readBuffer = ByteArray(1024)
        while (readPort.isOpen) {
            val numRead = readPort.readBytes(readBuffer, readBuffer.size)
            if (numRead > 0) {
                val receivedData = String(readBuffer, 0, numRead)
                println("Received: $receivedData")
            }
        }
    }

    val testData = "Hola mundo"
    writePort.outputStream.write(testData.toByteArray())
    writePort.outputStream.flush()
    println("Data sent: $testData")

    // Wait for a bit to ensure data is read
    Thread.sleep(1000)

    // Close ports
    writePort.closePort()
    readPort.closePort()
}

fun main() {

    val writePortName = "COM3"
    val readPortName = "COM4"

    testSerialPortCommunication(writePortName, readPortName)
}
