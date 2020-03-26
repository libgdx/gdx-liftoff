package com.github.czyzby.autumn.mvc.component.i18n.dto;

import java.util.Locale;

import com.github.czyzby.autumn.mvc.component.i18n.LocaleService;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Allows to change current application's locale.
 *
 * @author MJ */
public class LocaleChangingAction implements ActorConsumer<Void, Object> {
    private final LocaleService localeService;
    private final Locale locale;

    public LocaleChangingAction(final LocaleService localeService, final Locale locale) {
        this.localeService = localeService;
        this.locale = locale;
    }

    @Override
    public Void consume(final Object actor) {
        localeService.setCurrentLocale(locale);
        return null;
    }
}
