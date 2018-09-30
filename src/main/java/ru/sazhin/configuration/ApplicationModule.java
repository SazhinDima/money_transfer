package ru.sazhin.configuration;

import com.google.inject.Binder;
import com.google.inject.Module;
import ru.sazhin.service.TransactionService;
import ru.sazhin.service.impl.TransactionServiceImpl;

public class ApplicationModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(TransactionService.class).to(TransactionServiceImpl.class);
    }
}
