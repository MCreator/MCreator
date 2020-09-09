If you are interested in helping with MCreator development, you are welcome to support this project by 
opening pull requests. Even if you do not code, you can help by contributing translations, in-app tips, or
by [donating](https://mcreator.net/donate).

# Tips

* A good starting point into contributing is fixing or adding one of the features or bug reports from the issue tracker
* Before contributing new features into MCreator, consider opening a pull request draft, so we can discuss the suggested 
changes, and the way how to implement them to fit the existing code base and UX flow as good as possible.
* Try to follow existing code style, naming conventions, and UI/UX philosophy as much as possible
* When committing, make sure to reference the issue by #ID in the commit message to link commits to the issue
* Reference potential issues the pull request relates to when an issue(s) related to the pull request exists
* Make sure to update files with license headers
* Consider checking and testing existing PRs and confirm they work as designed to help us avoid bugs (try testing different MC versions, generators, also importing old workspaces, ...)
* _Check existing issues and consider fixing one before considering adding a brand new feature not discussed before_
* Be human, we are humans too, keep the community positive when colaborating with contributors and maintainers :)
* Maintainers of this project do this for hobby. There might be cases of slow responses, or even inability to review or merge PR due to its scope and our (time) inability to review it properly or assist with changes needed to make it acceptable for merge.

*Learn more about the development process and tips on [MCreator developers wiki](https://github.com/MCreator/MCreator/wiki).*

If you are looking for a good issues for a starter, check [good first issue candidates](https://github.com/MCreator/MCreator/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22)

# Pull requests

Once you complete your feature and are sure you followed the tips and tried to make the code blend in as much as possible, we will review the code.

The contributed code must pass all tests and be mergeable into the master branch. Expect comments on code after the code review. You will likely
need to change some code parts based on the maintainer's suggestions.

Some features might not be accepted into the core if they do not follow our guidelines, are low quality, or steer MCreator away from its roadmap 
or do not fit the current UX flow of the application. Too specific features that would make UI more complex, but would not be benefitical to the
most of the users might be rejected too, or suggested to be distributed in a plugin format.

If maintainers are busy, it can take a few days (or more) to properly review your PR so please be patient ;)

Some pull request tips and standards:
* Please separate different features in different pull requests
* If possible, prepare a changelog of your pull request that is ready to be used in the final MCreator changelog
* When adding features to generator, make sure to cover all generators currently supported
* Add tests for features you added with the PR, if new fields were added to mod elements, update TestWorkspaceDataProvider

# Localization (translation)

If you would like to contribute to the translations, make Crowdin user account and visit https://crowdin.com/project/mcreator. If you would like a brand new language added, open an issue or contact us otherwise and we will add it to the list of languages.

# CLA

Before we can use your code, you must sign the [MCreator CLA](https://cla-assistant.io/MCreator/MCreator), which you can do online.
The CLA is necessary mainly because you own the copyright to your changes, even after your contribution 
becomes part of our codebase, so we need your permission to use and distribute your code. We also need to be sure 
of various other things—for instance that you'll tell us if you know that your code infringes on other people's patents. 
You don't have to sign the CLA until after you've submitted your code for review and we approved it, but you must do it before
 we can put your code into our codebase.
