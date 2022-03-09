#!/usr/bin/env java

import java.net.{URL, HttpURLConnection}
val url = sys.env.getOrElse("SCRAPE_URL", "http://localhost:9000/scrape")
val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
connection.setConnectTimeout(5000)
connection.setReadTimeout(5000)
connection.setRequestMethod("GET")
val inputStream = connection.getInputStream
val content = io.Source.fromInputStream(inputStream).mkString
if (inputStream != null) inputStream.close
println(content)
