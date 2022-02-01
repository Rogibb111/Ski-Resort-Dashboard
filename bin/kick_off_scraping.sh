#!/usr/bin/env scala.bat

import java.net.{URL, HttpURLConnection}
val connection = (new URL("http://www.google.com")).openConnection.asInstanceOf[HttpURLConnection]
connection.setConnectTimeout(5000)
connection.setReadTimeout(5000)
connection.setRequestMethod("GET")
val inputStream = connection.getInputStream
val content = io.Source.fromInputStream(inputStream).mkString
if (inputStream != null) inputStream.close
println(content)
