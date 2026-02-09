### Module 1: Coding Standards

Name: I Gusti Ngurah Agung Airlangga Putra

Class / NPM: B / 2406358794

### Reflection 1
In this project, I implemented the edit and delete product features using Spring Boot and followed several clean code principles that I learned in this module. I tried to keep the code simple and easy to read by separating the controller, service, and repository layers so each class has a clear responsibility. I also used meaningful method names like update, and delete so the purpose of each function is easy to understand. In addition, I avoided repeating logic by placing business processes inside the service layer instead of directly in the controller. For secure coding, I used POST requests for actions that change data such as edit and delete, and I avoided exposing internal data unnecessarily. I also added basic checks, such as redirecting to the product list page when a product is not found, to prevent errors.

However, after reviewing the code again, I noticed some parts that can still be improved. The repository currently stores data in a simple list without validation or persistence, which is not safe for real applications. The code also does not include proper error handling or input validation, so invalid data could still be submitted. In the future, this could be improved by adding validation, better exception handling, and using a real database instead of in-memory storage. Overall, this exercise helped me understand how clean structure, clear naming, and safe request handling can make code easier to maintain and more secure.

### Reflection 2

