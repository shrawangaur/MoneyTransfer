package com.shrawan.revolut;

import com.shrawan.revolut.api.AccountApiController;
import com.shrawan.revolut.domain.*;
import org.jooby.Jooby;
import org.jooby.apitool.ApiTool;
import org.jooby.json.Jackson;

/**
 * Apitool starter project:
 */
public class App extends Jooby {

    {
        use(new Jackson());

        /** A module with domain logic */
        use((env, conf, binder) -> {
            binder.bind(AccountRepository.class).to(InMemoryAccountRepository.class);
            binder.bind(AccountTransactionService.class).to(AccountTransactionServiceImpl.class);
            binder.bind(CurrencyRatesProvider.class).to(CurrencyRatesProviderImpl.class);
            binder.bind(TransferService.class).to(TransferServiceImpl.class);
        });

        /** Actual routes */
        use(AccountApiController.class);

        /** Export API to Swagger */
        use(new ApiTool()
                .swagger());

        /** Populate the repository with test accounts! */
        onStart(reg -> TestAccountsPopulation.populateAccounts(reg.require(AccountRepository.class)));

    }

    public static void main(final String[] args) {
        run(App::new, args);
    }

}
