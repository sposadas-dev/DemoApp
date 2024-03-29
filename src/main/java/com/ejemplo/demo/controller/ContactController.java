package com.ejemplo.demo.controller;

import com.ejemplo.demo.constant.ViewConstant;
import com.ejemplo.demo.model.ContactModel;
import com.ejemplo.demo.service.ContactService;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/contacts")
public class ContactController {

    private static final Log LOG = LogFactory.getLog(ContactController.class);

    @Autowired
    @Qualifier("contactServiceImpl")
    private ContactService contactService;

    @GetMapping("/cancel")
    public String cancel(){
        LOG.info("Method: cancel() --> Controller: ContactController");
        return "redirect:/contacts/showcontacts";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/contactform")
    public String redirectContactForm(@RequestParam(name="id", required = false) int id,
            Model model){
        LOG.info("Method: redirectContactForm()");
        ContactModel contact = new ContactModel();
        if(id != 0){
            contact = contactService.findContactByIdModel(id);
        }
        model.addAttribute("contactModel", contact);
        LOG.info("Returning /contactform");
        return ViewConstant.CONTACT_FORM;
    }

    @PostMapping("/addcontact")
    public String addContact(@ModelAttribute(name = "contactModel") ContactModel contactModel,
                                Model model){
        if(null != contactService.addContact(contactModel)){
            model.addAttribute("result", 1);
        } else {
            model.addAttribute("result", 0);
        }
        LOG.info("Method: addContact()" + " -- Params: " + contactModel.toString());
        return "redirect:/contacts/showcontacts";
    }

    @GetMapping("/showcontacts")
    public ModelAndView showContacts(){
        ModelAndView mav = new ModelAndView(ViewConstant.CONTACTS);
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mav.addObject("contacts", contactService.listAllContacts());
        mav.addObject("username", user.getUsername());
        return mav;
    }

    @GetMapping("/removecontact")
    public ModelAndView removeContact(@RequestParam(name="id", required = true) int id){
        contactService.removeContact(id);
        return showContacts();
    }

}
