package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/links")
public class UrlController {

    private final UrlService urlService;

    /**
     * Отображает форму для ввода полной ссылки.
     *
     * @param model Модель для передачи данных во view
     * @return Имя шаблона с формой
     */
    @GetMapping
    public String showShortenPage(Model model) {
        model.addAttribute("requestUrlDto", new RequestUrlDto());
        return "shorten";
    }

    @GetMapping("/{hash}")
    public void redirectShortLinkToFull(@PathVariable("hash") @NotBlank String hash,
                                        HttpServletResponse response) throws IOException {
        String originalUrl = urlService.getFullRedirectionLink(hash);

        response.sendRedirect(originalUrl);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    }

    @PostMapping
    public String convertLinkToShort(@ModelAttribute("requestUrlDto") @Valid RequestUrlDto requestUrlDto,
                                     BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "shorten";
        }

        ResponseUrlDto response = urlService.convertUrlToShort(requestUrlDto);
        String url = response.getUrl();
        String shortUrl = response.getShortUrl();

        model.addAttribute("url", url);
        model.addAttribute("shortUrl", shortUrl);
        return "shorten";
    }
}