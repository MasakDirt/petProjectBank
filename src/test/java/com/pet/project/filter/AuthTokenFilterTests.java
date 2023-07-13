package com.pet.project.filter;

import com.pet.project.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AuthTokenFilterTests {
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpServletResponse httpResponse;
    @Mock
    private FilterChain filterChain;
    @Spy
    private JwtUtils jwtUtils;

    private final AuthTokenFilter tokenFilter;

    @Value("${myJwtToken.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    public AuthTokenFilterTests(AuthTokenFilter tokenFilter) {
        this.tokenFilter = tokenFilter;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(jwtUtils).isNotNull();
        assertThat(httpRequest).isNotNull();
        assertThat(httpResponse).isNotNull();
        assertThat(filterChain).isNotNull();
        assertThat(tokenFilter).isNotNull();
    }

    @Test
    public void testDoFilterInternal_ValidToken_AuthenticationSet() throws ServletException, IOException {
        String token = jwtUtils.generateTokenFromUsername("nike@mail.co");

        doReturn(true).when(jwtUtils).validateJwtToken(token);
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + token);

        tokenFilter.doFilterInternal(httpRequest, httpResponse, filterChain);

        verify(filterChain, times(1)).doFilter(httpRequest, httpResponse);
        assert SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken;
    }

    @Test
    public void testDoFilterInternal_NotValidToken_AuthenticationSet() throws ServletException, IOException {
        String token = "dglmglmglkmrkmgr";

        doReturn(false).when(jwtUtils).validateJwtToken(token);
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + token);

        tokenFilter.doFilterInternal(httpRequest, httpResponse, filterChain);

        verify(filterChain, times(1)).doFilter(httpRequest, httpResponse);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }
}
