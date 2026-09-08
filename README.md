# LPL26 Compiler & Parser

A full compiler pipeline for a custom programming language (LPL26), 
built in Java. The project covers every stage of compilation: lexing, 
recursive-descent parsing, AST construction, and assembly code generation 
targeting a stack-based virtual machine (SSM).

## Technologies
- Java
- JUnit 5 (parameterised testing)
- Stack Machine Assembly (SSM)
- Object-Oriented Design

## Project Structure
- `parse/` — Recursive-descent parser that builds the AST from source code
- `compile/ast/` — 20+ AST node classes (expressions, statements, functions, arrays, records)
- `compile/` — Symbol table, code generation, and compiler entry point
- `test/` — Parameterised JUnit 5 test suite across 5 difficulty tiers (A–E)
- `data/` — Language definition files (.sbnf) and test programs

## How It Works
1. The parser reads LPL26 source code and builds an Abstract Syntax Tree
2. The AST is walked by each node's compile() method
3. SSM assembly instructions are emitted to a .ssma file
4. The assembler converts .ssma to a binary .ssm file
5. The SSM virtual machine executes the binary

## Key Features
- Recursive-descent parser handling expressions, statements, functions, 
  procedures, arrays, and records
- Scoped symbol table resolving global, local, and parameter variables 
  with correct frame-pointer addressing
- Runtime safety compiled into the output: null pointer checks, array 
  bounds guards, and heap exhaustion handling
- Parameterised JUnit 5 test suite validating both parser correctness 
  and full compile-and-execute behaviour
