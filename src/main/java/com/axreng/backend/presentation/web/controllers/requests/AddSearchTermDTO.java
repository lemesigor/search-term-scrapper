package com.axreng.backend.presentation.web.controllers.requests;

public class AddSearchTermDTO {

        private String keyword;

        public AddSearchTermDTO() {
        }

        public AddSearchTermDTO(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }
}
