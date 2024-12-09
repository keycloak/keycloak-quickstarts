# Keycloak Community

Keycloak is an Open Source Identity and Access Management solution for modern Applications and Services.

## Contributing to Keycloak Quickstarts

Here's a quick checklist for a good PR, more details below:

2. A GitHub Issue with a good description associated with the PR
3. One feature/change per PR
4. One commit per PR
5. PR rebased on main (`git rebase`, not `git pull`)
5. [Good descriptive commit message, with link to issue](#commit-messages-and-issue-linking)
6. No changes to code not directly related to your PR
7. Includes functional/integration test
8. Includes documentation

If you are contributing a new quickstart, please wait for feedback from our maintainers before doing any implementation. 
Make sure to describe in the issue the purpose of the quickstart and how it can help others with similar use cases.

Once you have submitted your PR please monitor it for comments/feedback. We reserve the right to close inactive PRs if
you do not respond within 2 weeks (bear in mind you can always open a new PR if it is closed due to inactivity).

Also, please remember that we do receive a fairly large amount of PRs and also have code to write ourselves, so we may
not be able to respond to your PR immediately.

WARNING: Please send pull requests to the branch `main`. The pull requests are not supposed to be sent to any other branch.

### Finding something to work on

If you would like to contribute to Keycloak, but are not sure exactly what to work on, you can find a number of open
issues that are awaiting contributions in  
[issues](https://github.com/keycloak/keycloak-quickstarts/issues).

### Create an issue

Take your time to write a proper issue including a good summary and description.

Remember this may be the first thing a reviewer of your PR will look at to get an idea of what you are proposing
and it will also be used by the community in the future to find about what new features and enhancements are included in
new releases.

### Implementing

Make sure the quickstart is simple enough and understandable by developers not so experienced as you.

Do not format or refactor code that is not directly related to your contribution. If you do this it will significantly
increase our effort in reviewing your PR. If you have a strong need to refactor code then submit a separate PR for the
refactoring.

### Testing

You must provide tests for a quickstart and make sure they are running in our CI. 

The tests should be written in its simplest form so that they can be maintained by us. 

### Documentation

Make sure the quickstart is properly documented from a `README.md` file that must be located at the root of the quickstart.

### Compatibility with server, client and adapter versions

We accept the pull requests sent to the `main` branch as stated above. The `main` branch has reference to the last released version
of Keycloak server, Keycloak client libraries and adapters (Node.JS adapter, Javascript adapter and SAML adapter). If your quickstart
works only with `nightly` version of the server or client libraries (typically when you are implementing quickstart for some very new feature,
which was just added in the `main` branch of server/client, but is not yet released), then it is still possible to implement your quickstart and
send the PR. In this case:
* Please mark the PR as draft PR
* Add the description in the PR about the fact that your PR works only with latest codebase, but not yet with released version of server/client/adapter.
Your quickstarts PR might be possibly accepted once the particular server/client/adapter version is released.

During testing your quickstart, you may need to update the particular version to SNAPSHOT (for example updating `keycloak.version` or `keycloak.client.version`
in the root [pom.xml](pom.xml) file), but please do that just during your own testing. Make sure to not update the version to SNAPSHOT in the final version of your PR.

### Submitting your PR

When preparing your PR make sure you have a single commit and your branch is rebased on the main branch from the
project repository.

This means use the `git rebase` command and not `git pull` when integrating changes from main to your branch. See
[Git Documentation](https://git-scm.com/book/en/v2/Git-Branching-Rebasing) for more details.

We require that you squash to a single commit. You can do this with the `git rebase -i HEAD~X` command where X
is the number of commits you want to squash. See the [Git Documentation](https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History)
for more details.

The above helps us review your PR and also makes it easier for us to maintain the repository. It is also required by
our automatic merging process.

Please, also provide a good description [commit message, with a link to the issue](#commit-messages-and-issue-linking).
We also require that the commit message includes a link to the issue ([linking a pull request to an issue](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue)).

### Commit messages and issue linking

The format for a commit message should look like:

```
A brief descriptive summary

Optionally, more details around how it was implemented

Closes #1234
``` 

The very last part of the commit message should be a link to the GitHub issue, when done correctly GitHub will automatically link the issue with the PR. There are 3 alternatives provided by GitHub here:

* Closes: Issues in the same repository
* Fixes: Issues in a different repository (this shouldn't be used, as issues should be created in the correct repository instead)
* Resolves: When multiple issues are resolved (this should be avoided)

Although, GitHub allows alternatives (close, closed, fix, fixed), please only use the above formats.

Creating multi line commit messages with `git` can be done with:

```
git commit -m "Summary" -m "Optional description" -m "Closes #1234"
```

Alternatively, `shift + enter` can be used to add line breaks:

```
$ git commit -m "Summary
> 
> Optional description
> 
> Closes #1234"
```

For more information linking PRs to issues refer to the [GitHub Documentation](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue).