package com.example.documents.pojo.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccountRolesResponse {
    List<Account> accounts;

    @Getter
    @Setter
    public static class Account {
        private Long id;
        private String name;
    }
}
