package com.hospital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@Controller
public class CaptchaController {
    
    private static final String CAPTCHA_KEY = "captcha";
    private static final int WIDTH = 120;
    private static final int HEIGHT = 44;
    private static final int CODE_LENGTH = 4;
    
    @GetMapping("/captcha")
    public void generateCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        Random random = new Random();
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        String captchaCode = "";
        for (int i = 0; i < CODE_LENGTH; i++) {
            captchaCode += String.valueOf(random.nextInt(10));
        }
        
        session.setAttribute(CAPTCHA_KEY, captchaCode);
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            g.drawString(String.valueOf(captchaCode.charAt(i)), 20 + i * 25, 30);
        }
        
        for (int i = 0; i < 10; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT), 
                     random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }
        
        g.dispose();
        
        response.setContentType("image/jpeg");
        ImageIO.write(image, "jpeg", response.getOutputStream());
    }
    
    public static boolean validateCaptcha(HttpSession session, String inputCaptcha) {
        String sessionCaptcha = (String) session.getAttribute(CAPTCHA_KEY);
        if (sessionCaptcha == null || inputCaptcha == null) {
            return false;
        }
        session.removeAttribute(CAPTCHA_KEY);
        return sessionCaptcha.equalsIgnoreCase(inputCaptcha);
    }
}