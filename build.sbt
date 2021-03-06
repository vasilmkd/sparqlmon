ThisBuild / name := "sparqlmon"
ThisBuild / organization := "mk.ukim.finki.wbs.sparqlmon"
ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "2.13.3"

val compilerOptions = Seq(
  "-deprecation",                  // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8",                         // Specify character encoding used by source files.
  "-explaintypes",                 // Explain type errors in more detail.
  "-feature",                      // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",        // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds",         // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked",                    // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                   // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings",              // Fail the compilation if there are any warnings.
  "-Xlint:adapted-args",           // Warn if an argument list is modified to match the receiver.
  "-Xlint:constant",               // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select",     // Selecting member of DelayedInit.
  "-Xlint:doc-detached",           // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",           // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",              // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",   // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-unit",           // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",        // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",         // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",            // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",  // A local type parameter shadows a type already in scope.
  "-Ywarn-dead-code",              // Warn when dead code is identified.
  "-Ywarn-extra-implicit",         // Warn when more than one implicit parameter section is defined.
  "-Ywarn-numeric-widen",          // Warn when numerics are widened.
  "-Ywarn-unused:implicits",       // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports",         // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals",          // Warn if a local definition is unused.
  "-Ywarn-unused:params",          // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars",         // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates",        // Warn if a private member is unused.
  "-Ywarn-value-discard"           // Warn when non-Unit expression results are unused.
)

lazy val sparqlmon = (project in file("."))
  .aggregate(messages, registration, availability, status, alerting, gateway)

lazy val messages = (project in file("messages"))
  .settings(
    scalacOptions ++= compilerOptions,
    test in assembly := {},
    libraryDependencies ++= Seq(
      "io.circe"        %% "circe-core"    % "0.13.0",
      "io.circe"        %% "circe-generic" % "0.13.0",
      "io.circe"        %% "circe-parser"  % "0.13.0",
      "com.github.fd4s" %% "fs2-kafka"     % "1.0.0",
      "org.tpolecat"    %% "doobie-core"   % "0.8.8",
      "com.sun.mail"     % "javax.mail"    % "1.6.2"
    )
  )

lazy val registration = (project in file("registration"))
  .settings(
    scalacOptions ++= compilerOptions,
    test in assembly := {},
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
    libraryDependencies ++= Seq(
      "org.http4s"    %% "http4s-blaze-server" % "0.21.6",
      "org.http4s"    %% "http4s-blaze-client" % "0.21.6",
      "org.http4s"    %% "http4s-dsl"          % "0.21.6",
      "org.http4s"    %% "http4s-circe"        % "0.21.6",
      "io.circe"      %% "circe-literal"       % "0.13.0",
      "org.tpolecat"  %% "doobie-postgres"     % "0.8.8",
      "org.tpolecat"  %% "doobie-hikari"       % "0.8.8",
      "org.slf4j"      % "slf4j-simple"        % "1.7.30",
      "org.scalameta" %% "munit"               % "0.7.9" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
  .dependsOn(messages)

lazy val availability = (project in file("availability"))
  .settings(
    scalacOptions ++= compilerOptions,
    test in assembly := {},
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
    libraryDependencies ++= Seq(
      "org.http4s"    %% "http4s-blaze-server" % "0.21.6",
      "org.http4s"    %% "http4s-blaze-client" % "0.21.6",
      "org.http4s"    %% "http4s-dsl"          % "0.21.6",
      "org.http4s"    %% "http4s-circe"        % "0.21.6",
      "io.circe"      %% "circe-literal"       % "0.13.0",
      "org.tpolecat"  %% "doobie-postgres"     % "0.8.8",
      "org.tpolecat"  %% "doobie-hikari"       % "0.8.8",
      "org.slf4j"      % "slf4j-simple"        % "1.7.30",
      "org.scalameta" %% "munit"               % "0.7.9" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
  .dependsOn(messages)

lazy val status = (project in file("status"))
  .settings(
    scalacOptions ++= compilerOptions,
    test in assembly := {},
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
    libraryDependencies ++= Seq(
      "org.http4s"    %% "http4s-blaze-server" % "0.21.6",
      "org.http4s"    %% "http4s-dsl"          % "0.21.6",
      "org.http4s"    %% "http4s-circe"        % "0.21.6",
      "org.tpolecat"  %% "doobie-postgres"     % "0.8.8",
      "org.tpolecat"  %% "doobie-hikari"       % "0.8.8",
      "org.slf4j"      % "slf4j-simple"        % "1.7.30",
      "org.scalameta" %% "munit"               % "0.7.9" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
  .dependsOn(messages)

lazy val alerting = (project in file("alerting"))
  .settings(
    scalacOptions ++= compilerOptions,
    test in assembly := {},
    libraryDependencies ++= Seq(
      "org.slf4j"      % "slf4j-simple" % "1.7.30",
      "org.scalameta" %% "munit"        % "0.7.9" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
  .dependsOn(messages)

lazy val gateway = (project in file("gateway"))
  .settings(
    scalacOptions ++= compilerOptions,
    test in assembly := {},
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % "0.21.6",
      "org.http4s" %% "http4s-blaze-client" % "0.21.6",
      "org.http4s" %% "http4s-dsl"          % "0.21.6",
      "org.slf4j"   % "slf4j-simple"        % "1.7.30"
    )
  )
  .dependsOn(messages)
