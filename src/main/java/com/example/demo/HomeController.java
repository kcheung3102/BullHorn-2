package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String messageList(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "index";
    }

    @PostMapping("/searchList")
    public String search(Model model, @RequestParam("search") String search ) {
        model.addAttribute("messages", messageRepository.findByContentContainingIgnoreCaseOrDateContainingIgnoreCase(search,search));
        return "searchList";
    }


    @GetMapping("/add")
    public String messageForm(Model model) {
        model.addAttribute("message", new Message());
        return "messageform";
    }

    @PostMapping("/process")
    public String processForm(@Valid Message message, BindingResult result) {
        if(result.hasErrors()) {
            return "messageform";
        }
        messageRepository.save(message);
        return "redirect:/";
    }

    @PostMapping("/add")
    public String processImage(@ModelAttribute Message message, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            message.setImage(null);
        } else {


            try {
                Map uploadResult = cloudc.upload(file.getBytes(),
                        ObjectUtils.asMap("resourcetype", "auto"));
                message.setImage(uploadResult.get("url").toString());

            } catch (IOException e) {
                e.printStackTrace();
                message.setImage(null);
            }
        }
        messageRepository.save(message);
        return "redirect:/";
    }


    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model) {
        model.addAttribute("message" , messageRepository.findById(id).get());
        return "show";
    }
    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id,
                                Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }

    @RequestMapping("/delete/{id}")
    public String deleteMessage(@PathVariable("id") long id){
        messageRepository.deleteById(id);
        return "redirect:/";
    }




}
