package com.shrawan.revolut.api;

import com.shrawan.revolut.domain.AccountRepository;
import com.shrawan.revolut.domain.InMemoryAccountRepository;
import com.shrawan.revolut.domain.TransferService;
import org.jooby.Err;
import org.jooby.Result;
import org.jooby.Results;
import org.jooby.mvc.Body;
import org.jooby.mvc.GET;
import org.jooby.mvc.POST;
import org.jooby.mvc.Path;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/accounts")
public class AccountApiController {

    private TransferService transferService;
    private AccountRepository accountRepository = new InMemoryAccountRepository() ;

    @GET
    public List<AccountDto> getAll() {
        return accountRepository.getAll().stream()
                .map(AccountDto::fromDomain)
                .sorted(Comparator.comparing(AccountDto::getAccountNumber))
                .collect(toList());
    }

    @Path("/{id}")
    @GET
    public AccountDto getOne(String id) {
        return accountRepository.findByAccountNumber(id)
                .map(AccountDto::fromDomain)
                .orElseThrow(() -> new Err(404));
    }

    @Path("/transfer")
    @POST
    public Result makeTransfer(@Body TransferRequest transferRequest) {
        transferService.makeTransfer(transferRequest.getAccountFrom(), transferRequest.getAccountTo(), transferRequest.getAmount());
        return Results.ok();
    }

}
