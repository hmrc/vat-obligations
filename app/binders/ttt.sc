import java.time.LocalDate

val head: String = "Fd"
val values = Seq(head, "vvv")

val res = values.head match {
  case data if data.matches("^[F,O]{1}$") => true
  case _ => false
}

val date = "2017-06-30"
val xxxx = LocalDate.parse(date)
//val xx= if (data.matches("^[F,O]{1}$")) true else false
///
res

xxxx
val day = s"month : ${xxxx.getDayOfMonth}"
val month = s"month : ${xxxx.getMonthValue}"
val year = s"month : ${xxxx.getYear}"

day
month
year


