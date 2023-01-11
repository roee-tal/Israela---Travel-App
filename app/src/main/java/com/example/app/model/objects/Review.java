package com.example.app.model.objects;

public class Review {

        private String name;
        private String review;

        public Review() {}

        public Review (String name, String review) {

                this.name = name;
                this.review = review;
        }

        public String getName() {
                return name;
        }

        public String getReview() {
                return review;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) {
                        return true;
                }

                if (o == null || getClass() != o.getClass()) {
                        return false;
                }

                Review other = (Review) o;
                return name.equals(other.name) && review.equals(other.review);
        }
}
