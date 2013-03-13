package com.generalidevelopment.hit2013.web;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.generalidevelopment.hit2013.Item;

@RequestMapping("/items")
@Controller
public class ItemController {

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String create(@Valid final Item item, final BindingResult bindingResult, final Model uiModel,
			final HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, item);
			return "items/create";
		}
		uiModel.asMap().clear();
		item.persist();
		return "redirect:/items/" + encodeUrlPathSegment(item.getId().toString(), httpServletRequest);
	}

	@RequestMapping(params = "form", produces = "text/html")
	public String createForm(final Model uiModel) {
		populateEditForm(uiModel, new Item());
		return "items/create";
	}

	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") final Long id, final Model uiModel) {
		uiModel.addAttribute("item", Item.findItem(id));
		uiModel.addAttribute("itemId", id);
		return "items/show";
	}

	@RequestMapping(produces = "text/html")
	public String list(@RequestParam(value = "page", required = false) final Integer page,
			@RequestParam(value = "size", required = false) final Integer size, final Model uiModel) {
		if (page != null || size != null) {
			final int sizeNo = size == null ? 10 : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			uiModel.addAttribute("items", Item.findItemEntries(firstResult, sizeNo));
			final float nrOfPages = (float) Item.countItems() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
					: nrOfPages));
		} else {
			uiModel.addAttribute("items", Item.findAllItems());
		}
		return "items/list";
	}

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@Valid final Item item, final BindingResult bindingResult, final Model uiModel,
			final HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, item);
			return "items/update";
		}
		uiModel.asMap().clear();
		item.merge();
		return "redirect:/items/" + encodeUrlPathSegment(item.getId().toString(), httpServletRequest);
	}

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String updateForm(@PathVariable("id") final Long id, final Model uiModel) {
		populateEditForm(uiModel, Item.findItem(id));
		return "items/update";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String delete(@PathVariable("id") final Long id,
			@RequestParam(value = "page", required = false) final Integer page,
			@RequestParam(value = "size", required = false) final Integer size, final Model uiModel) {
		final Item item = Item.findItem(id);
		item.remove();
		uiModel.asMap().clear();
		uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
		uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
		return "redirect:/items";
	}

	void populateEditForm(final Model uiModel, final Item item) {
		uiModel.addAttribute("item", item);
	}

	String encodeUrlPathSegment(String pathSegment, final HttpServletRequest httpServletRequest) {
		String enc = httpServletRequest.getCharacterEncoding();
		if (enc == null) {
			enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
		}
		try {
			pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
		} catch (final UnsupportedEncodingException uee) {
		}
		return pathSegment;
	}
}
