import app.AppUserPasswordEncoderListener
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

beans = {
    appUserPasswordEncoderListener(AppUserPasswordEncoderListener)
    passwordEncoder(BCryptPasswordEncoder)
}
