package alexa

import alexa.AlexaVolumes.AlexaVolumeVal
import org.scalatest.{WordSpec, _}

class AlexaVolumeSpec extends WordSpec with ShouldMatchers {

  "poke" in {
    println(AlexaVolumes.EIGHT)
    println(AlexaVolumes.values)
    println(AlexaVolumes(5))
    println(AlexaVolumes(7).id)
    println(AlexaVolumes.values.contains(AlexaVolumeVal(10)))
    println(AlexaVolumes.withName("Volume at level 2").id)

    println(AlexaVolumes.EIGHT.map(l => l + 1))

    val louder: Int => AlexaVolume = { l => AlexaVolumes(l + 1)}
    println(AlexaVolumes.NINE.flatMap(louder))

    val lower: Int => AlexaVolume = { l => AlexaVolumes(l - 1)}
    println(AlexaVolumes.ONE.flatMap(lower))
  }


}
