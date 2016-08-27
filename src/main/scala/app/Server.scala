package app

import akka.http.scaladsl.Http
import app.service.AkkaHttpMicroservice
import app.service.alexa.VolumeUserService


object Server extends App with AkkaHttpMicroservice with VolumeUserService {
  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
