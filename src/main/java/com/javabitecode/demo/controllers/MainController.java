package com.javabitecode.demo.controllers;

import com.javabitecode.demo.domain.Message;
import com.javabitecode.demo.domain.User;
import com.javabitecode.demo.repos.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class MainController {
    @Value("${upload.path}") //from properties
    private String uploadPath;

    private MessageRepository messageRepository;
    @Autowired
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/")
    public String greeting(Model model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam (required = false, defaultValue = "")String filter, Model model){
        Iterable<Message> messages = messageRepository.findAll();

        if (filter!=null && !filter.isEmpty()) {
            messages = messageRepository.findByTeg(filter);
        }else {
            messages = messageRepository.findAll();
        }
        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);    // test
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag, Model model,
            @RequestParam ("file")MultipartFile file) throws IOException {
        Message message = new Message(text, tag, user);

        if( file != null && !file.getOriginalFilename().isEmpty()){
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()){
                uploadDir.mkdir(); //Creates the directory named by this abstract pathname.
            }
            String uuidFile = UUID.randomUUID().toString();  //length = 36 symbols. Есть версии!
            String resultFilename = uuidFile + "." + file.getOriginalFilename(); //Returns the original file name, as provided by the browser.

                file.transferTo(new File(uploadPath + "/" + resultFilename)); //Transfers bytes from this channel's file to the given writable byte channel. Метод transferTo(OutputStream) используется для считывания всех bytes из текущего InputStream и записи их в указанный объект OutputStream, а также для возврата количества bytes, переданных в OutputStream. После этого текущий объект InputStream будет находиться в конце stream. Этот метод не закроет ни текущий объект InputStream, ни объект OutputStream
                message.setFilename(resultFilename);

        }

        messageRepository.save(message);

        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);

        return "main";
    }


}