# React Extra Credit
a. Did you use AI to code the UI? If so, what were the sources that the AI used, what was the AI good at and what was it not so good at? What did you do to fill in the gaps.

- I used Gemini to generate the Maven `pom.xml` in order to download and launch Springboot.
- I used Gemini for helping fix aligment and UI bugs in the React frontend.
- For installing all the packages required, Gemini didn't really do so well. I just used the developer docs to install what I needed.
- AI did pretty well with fixing my UI, probably because I know nothing about writing frontend code.

b. If you did not use AI, what sources did you use to learn React, and what were the hardest things to learn? 

- N/A.

c. We are planning to cover React next semester for this class, in what unit do you think this would be appropriate to teach?

- I think they teach React in Software Lab, but if you were to teach it next semester, it would make sense to have it along side or after Java Swing.

# Step By Step Instructions

## Dependencies
- Maven
    - Download the binary from [here](https://maven.apache.org/download.cgi)
    - Extract the folder and add it to the sytem PATH.
    - Alternatively, reference the docs [here](https://maven.apache.org/install.html) (most system package managers should support installing Maven)
    - `cd` into the repository root directory and run `mvn clean install`. This should pull all dependencies down for the Springboot application.
- Bun
    - Follow instruction [here](https://bun.com/get).
    - *note: I used Windows sho there is a powershell one-liner to install it. If you're on a unix based system, [install npm first](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm).*
    - `cd` into the `longhorn-network` directory and run `bun install` to pull all dependencies down for the React application.

## Starting the Springboot server
- `cd` into the repository root and run `mvn spring-boot:run`
- The Springboot backend API should now be running on `localhost:8080`

## Starting the React Frontend
- `cd` into the `longhorn-network` directory and run `bunx --bun vite`
- The React frontend UI should now be running on `localhost:5173`

## Visiting the page
- Go to `http://localhost:5173/` in a browser, make sure both the React and Springboot apps are running.