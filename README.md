# Coding Challenges

A collection of diverse coding challenges to enhance your problem-solving skills and coding proficiency.
Perfect for interview prep, contests, and skill improvement.

## Project Description

This repository contains solutions to various coding challenges implemented in Java 21.
The main goal of this repository is to provide a comprehensive collection of solutions for different coding problems,
showcasing clean code and thorough testing practices.

## Structure

The repository is organized into multiple subprojects, each representing a different challenge or topic.
Each subproject contains its own `README.md` with specific details about the implementation and testing.

### Subprojects

- **Camunda**: Solutions related to BPMN (Business Process Model and Notation) diagrams traversal.
    - **Acceptance Criteria Solution**: Basic implementation meeting the specified acceptance criteria.
    - **Clean Design Solution**: Enhanced implementation following clean design principles.

## Features

- Solutions to various coding challenges.
- Implementation of algorithms and design patterns in clean Java code.
- Comprehensive test cases for each implementation.
- Detailed documentation for each subproject.
- Full test coverage for all implementations.

## Requirements

- Java 21
- Maven (for building and running tests)

## Usage

Clone the repository and navigate to the specific subproject directory. Follow the instructions in the
subproject's `README.md` to compile and run the code.

```bash
git clone <repository-url>
cd coding-challenges
```

### Running a Specific Subproject

1. Navigate to the subproject directory:

    ```bash
    cd camunda/<subproject>
    ```

   Replace `<subproject>` with the path to the specific subproject you want to run. For example:

    ```bash
    cd camunda/acceptance-criteria-solution
    ```

2. Build the project using Maven:

    ```bash
    mvn clean install
    ```

3. Run the application with the required arguments:

    ```bash
    java -jar target/<subproject>.jar <startNodeId> <endNodeId>
    ```

   Replace `<subproject>` with the specific jar file name and `<startNodeId>` and `<endNodeId>` with the actual node IDs
   you want to use for finding the path in the BPMN diagram. For example:

    ```bash
    java -jar target/acceptance-criteria-solution.jar approveInvoice invoiceProcessed
    ```

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Acknowledgements

This project was inspired by real-world coding challenges and problem descriptions, aiming to deepen the understanding
of data structures, algorithms, and design patterns in Java.