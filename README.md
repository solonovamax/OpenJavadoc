# Open JavaDoc

A FOSS clone of [javadoc.io](https://javadoc.io/).

Ths project is a fork of [javadocky](https://github.com/KengoTODA/javadocky/), as the author was not very welcoming to contributions or suggestions regarding their software.
It is forked from commit `75155b8fa101c8770c0f756d6523fd29b269aacc`, as all code after that was relicensed under the AGPL instead of the Apache license.

## TODO

- [ ] Rewrite README
- [ ] Ability to specify multiple javadoc sources.
   See: [javadocky#295](https://github.com/KengoTODA/javadocky/issues/295)
- [ ] Simple UI Changes
   See: [javadocky#294](https://github.com/KengoTODA/javadocky/issues/294)
   - [ ] Left align group and artifact labels
   - [ ] Replace name with home button
   - [ ] Button to ease linking to the javadoc
   - [ ] Custom icon
   - [ ] Custom homepage text
   - [ ] Input gradle artifact coordinates
   - [ ] Button to download javadoc jar
- [ ] Finish badge generator
   See: [javadocky#293](https://github.com/KengoTODA/javadocky/issues/293)
- [ ] Support all shields.io styles and parameters
   See: [javadocky#292](https://github.com/KengoTODA/javadocky/issues/292)
- [ ] Modify page URL when navigating javadoc
   See: [javadocky#291](https://github.com/KengoTODA/javadocky/issues/291)
- [ ] Remove scroll bar outside of embeded iframe
   See: [javadocky#290](https://github.com/KengoTODA/javadocky/issues/290)
- [ ] Include artifact in dropdown on first load of page
  See: [javadocky#289](https://github.com/KengoTODA/javadocky/issues/289)
- [ ] No other artifacts are presented if no other repositories with the same group id have been loaded.
  See: [javadocky#288](https://github.com/KengoTODA/javadocky/issues/288)

# Old README

This project is a clone of [javadoc.io](http://javadoc.io/).
This is also a sandbox project to play with spring-boot v2.5.2, spring-webflux v5.3.8 and selenide v5.2.8.

![Build Status](https://github.com/KengoTODA/javadocky/workflows/Build/badge.svg)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=javadocky&metric=coverage)](https://sonarcloud.io/dashboard?id=javadocky)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/14582f2758734bd3a9c2076e210e4174)](https://www.codacy.com/gh/KengoTODA/javadocky/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=KengoTODA/javadocky&amp;utm_campaign=Badge_Grade)

## How to build

```sh
$ docker-compose up --build
```

You can visit [http://localhost:8080/](http://localhost:8080/) to enjoy service.


### How to configure

You can [set property](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html), to configure Javadocky.

|name                      |default value                     |note    |
|--------------------------|----------------------------------|--------|
|javadocky.maven.repository|https://repo.maven.apache.org/maven2/  |URL of the Maven repository to download javadoc.jar|

## License

Copyright 2017-2022 Kengo TODA

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
