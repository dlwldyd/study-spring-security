package springsecurity.core.security.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import springsecurity.core.security.common.FormWebAuthenticationDetails;
import springsecurity.core.security.service.AccountContext;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    //인증에 대한 검증 로직이 들어감
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //authentication 에는 사용자가 입력한 아이디, 패스워드 정보가 들어있음
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, accountContext.getPassword())) {
            throw new BadCredentialsException("BadCredentialsException");
        }

        /*
        뷰에서 히든 필드로 secretKey 라는 파라미터로 "secret"이라는 문자열을 넘기는데 만약 secretKey 라는 파라미터에
        "secret"이라는 문자열이 없으면 예외를 발생시킴
         */
        FormWebAuthenticationDetails details = (FormWebAuthenticationDetails) authentication.getDetails();
        String secretKey = details.getSecretKey();
        if (secretKey == null || !secretKey.equals("secret")) {
            throw new InsufficientAuthenticationException("InsufficientAuthenticationException");
        }

        //인자가 2개인(권한 정보X) 생정자는 로그인 시도 시 사용되는 생성자이다.
        //인자가 3개인(권한 정보O) 생성자는 로그인 성공 시 인증 토큰을 만들기 위해 사용되는 생성자이다.
        //인자 : principal -> 사용자 정보, credential -> 패스워드 정보, authorities -> 권한 정보
        return new UsernamePasswordAuthenticationToken(
                accountContext.getAccount(),
                null,
                accountContext.getAuthorities());
    }

    //인증 토큰을 해당 provider 가 인증처리를 지원하는지
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
