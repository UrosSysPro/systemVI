import java.io.{File, FileWriter}
import java.util.Scanner

@main def main(): Unit =
  val file = File("inputs.txt")
  val scanner = Scanner(file)
  var lines = List.empty[String]

  while scanner.hasNextLine do
    lines :+= scanner.nextLine()
  scanner.close()

  var pairs = lines.map { line =>
    val entries = line.split(" ")
    var underScore = false

    val name = entries(2).toLowerCase.foldLeft("") { (acc, c) =>
      c match
        case '_' => underScore = true; acc
        case _ => acc :+ (if underScore then {
          underScore = false
          c.toUpper
        } else {
          c
        });
    }

    val value = entries(4)

    (name, value)
  }

  val keywords=List("key","mod","joystick","hat")

  pairs=pairs.filter { (name, _) =>
    keywords.foldLeft(false){(acc,keyword)=>acc || name.contains(keyword)}
  }

  val output=FileWriter(File("output.txt"))
  pairs.foreach((name,value)=>{
    output.write(s"val $name:Int = $value\n")
  })
  output.close()

