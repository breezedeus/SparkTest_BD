
import java.util.Scanner

import scala.reflect.io.File

object MyTest {

  def withScanner(f: File, op: Scanner => Unit) = {
    val scanner = new Scanner(f.bufferedReader)
    try {
      op(scanner)
    } finally {
      scanner.close()
    }
  }

  def main(args: Array[String]) {
    println("Hello, Scala!");
    //withScanner(File("/proc/self/stat"),
     // scanner => println("pid is " + scanner.next()))
  }
}