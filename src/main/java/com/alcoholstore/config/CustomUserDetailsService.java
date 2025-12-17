//package com.alcoholstore.config;
//
//import com.alcoholstore.model.User;
//import com.alcoholstore.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + email));
//
//        if (!user.getEnabled()) {
//            throw new UsernameNotFoundException("Пользователь заблокирован: " + email);
//        }
//
//        String role = user.getRole();
//        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getEmail(),
//                user.getPassword(), // Пароль в чистом виде
//                Collections.singletonList(new SimpleGrantedAuthority(authority))
//        );
//    }
//}