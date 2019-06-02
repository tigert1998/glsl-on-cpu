# OpenGL Shader on CPU

## build

```bash
$ gradle build
$ java -jar build/libs/glsl-on-cpu-${VERSION}.jar -h
usage: glsl-on-cpu
 -a,--dump-ast      Dump debugging AST
 -h,--help          Helping info
 -i,--input <arg>   Input file path
 -v,--verbose       Print verbose scope and function information
$ java -jar build/libs/glsl-on-cpu-${VERSION}.jar -i ${PATH_TO_INPUT_FILE} 
```

## demos

| title and author                                             | snapshot                        | reference                                                    |
| ------------------------------------------------------------ | ------------------------------- | ------------------------------------------------------------ |
| 8-queens puzzle by me                                        | yes<br/>0, 4, 7, 5, 2, 6, 1, 3, | [8_queens.fs](demos/8_queens.fs)<br>[8_queens.c](demos/8_queens.c) |
| ["Pikachu" by nyri0@shadertoy](https://www.shadertoy.com/view/3tfGWl) | ![](resources/pikachu.png)      | [pikachu.fs](demos/pikachu.fs)<br>[rendering_main.cc](demos/rendering_main.cc) |
| ["The Drive Home" by Martijn Steinrucken aka BigWings](https://www.shadertoy.com/view/MdfBRX) | ![](resources/drive.png)        | [drive.fs](demos/drive.fs)<br/>[rendering_main.cc](demos/rendering_main.cc) |

## to-do

- [ ] comma expression
- [ ] macro
- [ ] postfix increment/decrement
- [x] switch
- [ ] uniform, texture, etc (rendering pipeline related)
- [ ] built-in functions (`mix`, `fract`, etc)

## additional feature

- in-line structure definition
- extern "C" and demangling

