name := "pfamskos"

version := "1.0.0"

scalaVersion := "2.9.1"

resolvers += "Oracle Repository" at "http://download.oracle.com/maven/"

libraryDependencies += "org.codehaus.woodstox" % "woodstox-core-asl" % "4.1.1"

libraryDependencies += "org.codehaus.staxmate" % "staxmate" % "2.0.0"

libraryDependencies += "com.sleepycat" % "je" % "5.0.34"

libraryDependencies += "junit" % "junit" % "4.8" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.8" % "test"

