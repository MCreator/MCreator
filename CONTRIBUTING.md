If you are interested in helping with MCreator development, you are welcome to support this project by 
opening pull requests. Even if you do not code, you can help by [contributing translations](https://translate.mcreator.net/), [in-app tips](https://github.com/MCreator/MCreator/tree/master/plugins/mcreator-localization/help), or
by [donating](https://mcreator.net/donate).

# General contributing tips

* **Always prioritize bug fixes and Minecraft version updates before new features. We may close new features when bug fixes or generator updates are needed due to limited reviewing resources we have.**
* **Check, try, test existing PRs and leave feedback** of other contributors and leave feedback on them (test on different Minecraft versions, test on your workspaces, ...). This helps maintainers with the reviewing process a lot.
* A good starting point into contributing is fixing or adding one of the features requested on our forums or bug reports from the issue tracker
* Follow existing code style, naming conventions, and UI/UX philosophy as much as possible, texts in UI should be written "Everywhere like this" and not "Do Not Write Like This"
* When adding features to code generators, make sure to cover all generators currently supported
* Maintainers of this project do this for a hobby. There might be cases of slow responses, or even inability to review or merge PR due to its scope and our (time) inability to review it properly or assist with changes needed to make it acceptable for merge.
* Be human, we are humans too, keep the community positive when colaborating with contributors and maintainers :)

# Pull request rules

Some features might not be accepted into the core if they do not follow our guidelines, are low quality, or steer MCreator away from its roadmap 
or do not fit the current UX flow of the application. Too specific features that would make UI more complex, but would not be beneficial to the
most of the users might be rejected too, or suggested to be distributed in a plugin format. Expect comments on code after the code review. You will likely
need to change some code parts based on the maintainer's suggestions.

General rules on pull requests are:

* The contributed code must pass all tests and be mergeable into the master branch.
* **Separate different features in different pull requests.**
* **Surprise PRs will likely get closed.** This means PRs that alter a lot of code or change UX dynamics without discussion with maintainers will likely get closed. These PRs usually diverge from our roadmap too much and/or take too much resources to review compared to benefit aligned with the roadmap. We are aware these PRs usually take a lot of time and effort to make, but sometimes hard decisions need to be made.
* **Avoid big PRs.** Similar as the previous rule. They will be likely take much more time to review. Large PRs might not be accepted due to amount of code reviewing and testing needed. Similar rule applies to PRs that significantly change core implementation.
* **PRs with low code quality, bugs (or code that seems untested), may be closed without further comments.** PRs from contributors breaking this rule multiple times may no longer be accepted.
* Prepare a changelog of your pull request that is ready to be used in the final MCreator changelog
* Add tests for features you added with the PR, if new fields were added to mod elements, update TestWorkspaceDataProvider
* If a contributor opens multiple new PRs without finishing old ones, we may assume the older PRs are no longer in their interest and close them, so other contributors and maintainers don't spend too much time reviewing PRs that will likely not get merged down the road.

Some more useful resources on PRs and contributing code that help the keep code and community spirit better (worth reading as these tips apply to other open-source projects too):

* [A Polite Guide to Pull Requests](https://thenewstack.io/code-n00b-polite-guide-pull-requests/)
* [The (written) unwritten guide to pull requests](https://www.atlassian.com/blog/git/written-unwritten-guide-pull-requests)
* [Pull Request Etiquette for Reviewers and Authors](https://betterprogramming.pub/pull-request-etiquettes-for-reviewer-and-author-f4e80360f92c)

Getting started tips on actual code development for MCreator can be found on [MCreator developers wiki](https://github.com/MCreator/MCreator/wiki).

# CLA

Before we can use your code, you must sign the [MCreator CLA](https://cla-assistant.io/MCreator/MCreator), which you can do online.
The CLA is necessary mainly because you own the copyright to your changes, even after your contribution 
becomes part of our codebase, so we need your permission to use and distribute your code. We also need to be sure 
of various other things—for instance that you'll tell us if you know that your code infringes on other people's patents. 
You don't have to sign the CLA until after you've submitted your code for review and we approved it, but you must do it before
 we can put your code into our codebase.
