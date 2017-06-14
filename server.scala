package unitec


//import kotlin.Metadata
//import kotlin.TypeCastException
//import kotlin.io.FilesKt
//import kotlin.jvm.internal.Intrinsics
//import kotlin.text.StringsKt
//import org.jetbrains.annotations.NotNull
//import spark.Request
//import spark.Response
//import spark.Route
//import spark.Spark._
//import spark._

//import javax.servlet.Filter
//import skinny.micro._

//import javax.servlet.ServletContext
//import org.scalatra._

//import org.eclipse.jetty.server.Server
//rt org.eclipse.jetty.servlet.DefaultServlet
//import org.eclipse.jetty.webapp.WebAppContextimpo
//import org.scalatra.servlet.ScalatraListener



import org.json.simple.JSONObject
import org.json.simple.JSONArray
import org.json.simple.parser.ParseException
import org.json.simple.parser.JSONParser
//
import java.io.IOException
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Base64
import java.lang.Object
import javax.imageio.ImageIO
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.Raster
import java.awt.Color

import util.control.Breaks._
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File
//
import com.google.maps._
import com.google.maps.model._
import com.google.maps.model.TravelMode
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.io.InputStream
import java.util

import java.nio.charset.Charset
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.io.OutputStream
import java.net.InetSocketAddress
import javax.imageio.ImageIO
import java.util.Base64


class ejercicio1() extends HttpHandler{
  override def handle(t: HttpExchange){
    if (t.getRequestMethod() == "POST") {
      println("ejericicio1")
      var response = "Http 200{\"ruta\":["
      val os: OutputStream = t.getResponseBody()
      var input = t.getRequestBody()
      var json_directions: Array[Byte] = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte).toArray
      val json_string = new String(json_directions, Charset.forName("UTF-8"))
      val parts = json_string.split("\"")
      val origin = parts(3)
      val destiny = parts(7)
      val context = new GeoApiContext().setApiKey("AIzaSyAUOMk8n8nhxUiSTEXq06jNth_kiV_s55E")
      var results = DirectionsApi.newRequest(context).origin(origin).destination(destiny).mode(TravelMode.DRIVING).await()
      var cont = 0
      for(i <- results.routes(0).legs(0).steps ) {
        if(cont != 0)
          response+=","
        response += "{\"lat\":"+ i.startLocation.lat+","+"\"lng\":"+i.startLocation.lng+"},"
        response += "{\"lat\":"+ i.endLocation.lat+","+"\"lng\":"+i.endLocation.lng+"}"
        cont+=  1
      }
      response+="]}"
      println(response)
      json_directions = response.getBytes(Charset.forName("UTF-8"))
      t.getResponseHeaders().add("content-type", "json")
      t.sendResponseHeaders(200, response.size.toLong)
      os.write(json_directions)
      os.close()
    }
  }
}

class ejercicio2() extends HttpHandler{
  override def handle(t: HttpExchange){
    if (t.getRequestMethod() == "POST") {
      println("ejericicio2")
      //recibir
      var response = "Http 200{\"restaurantes\":["
      val os: OutputStream = t.getResponseBody()
      var input = t.getRequestBody()
      var json_directions: Array[Byte] = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte).toArray
      val json_string = new String(json_directions, Charset.forName("UTF-8"))
      val parts = json_string.split("\"")
      val origin = parts(3)
      val context = new GeoApiContext().setApiKey("AIzaSyAUOMk8n8nhxUiSTEXq06jNth_kiV_s55E")
      val location = GeocodingApi.newRequest(context).address(origin).await()(0).geometry.location
      val result = PlacesApi.nearbySearchQuery(context, location).radius(5000).`type`(PlaceType.RESTAURANT).awaitIgnoreError
      var cont = 0
      for(i <- result.results ) {
        if(cont != 0)
          response+=","
        response += "{\"lat\":"+ i.geometry.location.lat+","+"\"lng\":"+i.geometry.location.lng+"}"
        cont+=  1
      }
      response+="]}"
      json_directions = response.getBytes(Charset.forName("UTF-8"))
      t.getResponseHeaders().add("content-type", "json")
      t.sendResponseHeaders(200, response.size.toLong)
      os.write(json_directions)
      os.close()
    }
  }
}

class ejercicio3() extends HttpHandler{
  override def handle(t: HttpExchange){
    if (t.getRequestMethod() == "POST") {
      var response = ""
      val os: OutputStream = t.getResponseBody()
      var input = t.getRequestBody()
      var json_directions: Array[Byte] = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte).toArray
      val json_string = new String(json_directions, Charset.forName("UTF-8"))
      val parts = json_string.split("\"")
      val filename = parts(3)
      val base64Data = parts(7)

      val base64decodedBytes = Base64.getDecoder.decode(base64Data)
      val img = ImageIO.read(new ByteArrayInputStream(base64decodedBytes))
      for (x <- 0 to img.getWidth() - 1 ){
        for (y <- 0 to img.getHeight() - 1 ){
          val c = new Color(img.getRGB(x,y))
          val red = c.getRed()
          val green = c.getGreen
          val blue = c.getGreen()
          val prom = (red + green + blue) / 3
          val newColor = new Color(prom, prom, prom).getRGB()
          img.setRGB(x, y, newColor )
        }
      }
      var nombre = filename
      var list = filename.split('.')
      nombre = list(0)+"(Blanco y Negro)."+list(1)

      val outputfile = new File(nombre)
      ImageIO.write(img , "bmp", outputfile)
      val base64encodedString = Base64.getEncoder().encodeToString( Files.readAllBytes( outputfile.toPath ) )
      response = "Http 200{\"nombre\" :\""+nombre+"\", \"data\":\""+base64encodedString+"\"}"

      json_directions = response.getBytes(Charset.forName("UTF-8"))
      t.getResponseHeaders().add("content-type", "json")
      t.sendResponseHeaders(200, response.size.toLong)
      os.write(json_directions)
      os.close()
    }
  }
}

class ejercicio4() extends HttpHandler{
  override def handle(t: HttpExchange){
    if (t.getRequestMethod() == "POST") {

      var response = ""
      val os: OutputStream = t.getResponseBody()
      var input = t.getRequestBody()
      var json_directions: Array[Byte] = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte).toArray
      val json_string = new String(json_directions, Charset.forName("UTF-8"))

      val parser = new JSONParser()
      val obj =  parser.parse(json_string)
      val jsonObject = obj.asInstanceOf[JSONObject]
      val img_Size = jsonObject.get("tamaño").asInstanceOf[JSONObject]

      println(img_Size)
      val parts = json_string.split("\"")
      val filename = jsonObject.get("nombre").asInstanceOf[String]
      val base64Data = jsonObject.get("data").asInstanceOf[String]
//      println("Filename   "+filename+"  data   "+base64Data)
      val salto = img_Size.get("alto")
      val sancho = img_Size.get("ancho")

      println("Filename   "+filename+"   Alto   "+salto + "   Ancho   "+sancho +" FIN")

      val alto = salto.toString().asInstanceOf[Long]
      val ancho = sancho.toString().asInstanceOf[Long]
      println("Filename   "+filename+"   Alto   "+alto + "   Ancho   "+ancho +" FIN")


      val base64decodedBytes = Base64.getDecoder.decode(base64Data)
      val img = ImageIO.read(new ByteArrayInputStream(base64decodedBytes))

      var newH = img.getHeight()/2
      var newW = img.getWidth()/2
      val tH = alto
      val tW = ancho
      var contx = 0
      var conty = 0
      var sendImg = img

      if (tW != null) {
        while( tW < newW  ){
          val image = new BufferedImage(newW, newW, BufferedImage.TYPE_INT_RGB)
          for(y <- 0 to newH-1){
            for (x <- 0 to newW-1){
              if( contx+2 < img.getWidth() && conty+2 < img.getHeight() ){
                val col1 = new Color(img.getRGB(contx, conty))
                val col2 = new Color(img.getRGB(contx+1, conty))
                val col3 = new Color(img.getRGB(contx, conty+1))
                val col4 = new Color(img.getRGB(contx+1, conty+1))
                val red = (col1.getRed() + col2.getRed() + col3.getRed() + col4.getRed())/4
                val green = (col1.getGreen() + col2.getGreen() + col3.getGreen() + col4.getGreen())/4
                val blue = (col1.getBlue() + col2.getBlue() + col3.getBlue() + col4.getBlue())/4
                image.setRGB(x, y, new Color(red, green, blue).getRGB() )
                contx += 2
              }
            }
            contx = 0
            conty += 2
          }
          newH /= 2
          newW /= 2
          sendImg = image
        }
      }
      var nombre = filename
      var list = filename.split('.')
      nombre = list(0)+"(Reducida)."+list(1)

      val outputfile = new File(nombre)
      ImageIO.write(sendImg , "bmp", outputfile)
      val base64encodedString = Base64.getEncoder().encodeToString( Files.readAllBytes( outputfile.toPath ) )
      response = "Http 200{\"nombre\" :\""+nombre+"\", \"data\":\""+base64encodedString+"\"}"

      json_directions = response.getBytes(Charset.forName("UTF-8"))
      t.getResponseHeaders().add("content-type", "json")
      t.sendResponseHeaders(200, response.size.toLong)
      os.write(json_directions)
      os.close()
    }
  }
}

object server extends App{
  var server = HttpServer.create(new InetSocketAddress(8080), 0)
  server.createContext("/ejercicio1", new ejercicio1())
  server.createContext("/ejercicio2", new ejercicio2())
  server.createContext("/ejercicio3", new ejercicio3())
  server.createContext("/ejercicio4", new ejercicio4())
  server.setExecutor(null)
  server.start()
}



//print("X")

//val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080
//
//val server = new Server(port)
//val context = new WebAppContext()
//context setContextPath "/"
//context.setResourceBase("src/main/webapp")
//context.setInitParameter(ScalatraListener.LifeCycleKey, "unitec.ScalatraBootstrap")
//context.addEventListener(new ScalatraListener)
//context.addServlet(classOf[DefaultServlet], "/")
//
//server.setHandler(context)
//
//server.start
//server.join
//  WebServer.mount(HelloApp).port(8080).start()