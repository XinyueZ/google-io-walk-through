Reference
=====
- 👷 Run `./gradlew check`  or  `./gradlew check connectedCheck` to validate whole project including sample app and library firstly.
- [Official document](https://developer.android.com/guide/slices/) 
- [Source code of official document](https://github.com/android/snippets/tree/master/slice) 
- [A demo project with Slices](https://github.com/CapTechMobile/Android-Slices)
- [Codelab of Slices](https://github.com/googlecodelabs/slices-basic-codelab)
- [Slice Viewer](https://github.com/googlesamples/android-SliceViewer)

# Code robust & healthy with klint support

The project uses [ktlint](https://ktlint.github.io/) to enforce Kotlin coding styles.
Here's how to configure it for use with Android Studio (instructions adapted
from the ktlint [README](https://github.com/shyiko/ktlint/blob/master/README.md)):

- Close Android Studio if it's open

- Download ktlint:

  `curl -sSLO https://github.com/shyiko/ktlint/releases/download/0.27.0/ktlint && chmod a+x ktlint`

- Inside the project root directory run:

  `ktlint --apply-to-idea-project --android`

- Remove ktlint if desired:

  `rm ktlint`

- Start Android Studio

# License

```
Copyright 2018 Chris Xinyue Zhao

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements. See the NOTICE file distributed with this work for
additional information regarding copyright ownership. The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```