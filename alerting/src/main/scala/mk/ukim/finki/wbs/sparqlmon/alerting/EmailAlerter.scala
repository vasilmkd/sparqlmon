package mk.ukim.finki.wbs.sparqlmon.alerting

import javax.mail._
import javax.mail.internet._

import cats.effect.Sync
import cats.implicits._

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

class EmailAlerter[F[_]: Sync] extends Alerter[F] {
  override def alert(ep: Endpoint): F[Unit] =
    for {
      password <- Sync[F].delay(System.getenv("SPARQLMON_PASSWORD"))
      _        <- Sync[F].delay {
             val to    = ep.email.toString
             val from  = "sparqlmon@gmail.com"
             val host  = "smtp.gmail.com"
             val props = System.getProperties()

             props.put("mail.smtp.host", host)
             props.put("mail.smtp.port", "465")
             props.put("mail.smtp.ssl.enable", "true")
             props.put("mail.smtp.auth", "true")

             val session = Session.getInstance(
               props,
               new Authenticator {
                 override protected def getPasswordAuthentication(): PasswordAuthentication =
                   new PasswordAuthentication("sparqlmon@gmail.com", password)
               }
             )

             try {
               val message = new MimeMessage(session)
               message.setFrom(new InternetAddress(from))
               message.addRecipients(Message.RecipientType.TO, to)
               message.setSubject("SPARQL endpoint down")
               message.setText(
                 s"The ${ep.url.toString} SPARQL endpoint has been unavailable for an hour. Please investigate for potential issues."
               )
               Transport.send(message)
             } catch {
               case e: MessagingException =>
                 e.printStackTrace()
             }
           }
    } yield ()
}
