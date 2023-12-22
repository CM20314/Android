# CM20314 - Android (Java)

## Get started
1. [Download and Install](https://developer.android.com/studio?gclid=CjwKCAiApuCrBhAuEiwA8VJ6JtYdGVqpRrWEiE5orle0YI5dG0VDqxCplSIUY8VUAHVrhPc6IP7KEBoCYbEQAvD_BwE&gclsrc=aw.ds) Android Studio. Ensure you also download the Virtual Android device(s) so you can test the code.
2. [Download and Install](https://git-scm.com/) `git`.
3. Go to your source directory and open the command line.
* On Windows, this is usually `C:\Users\USERNAME\source\repos`.
* On Mac, it's easier to just choose a folder of your liking where you want your code to be stored.
4. Run `git` to ensure git is installed correctly.
5. Run `git clone https://github.com/CM20314/Android`. This will create a folder within the folder you are in containing the solution code.
6. Run `cd Android`
6. Run `git checkout development`. **This is crucial.**

## Working on the project
**Please follow these steps *very carefully* to avoid unnecessary headaches.**
1. Before opening Android Studio, go to `[your source directory]\Android` and open the command line.
2. Run `git checkout -b feature-issue-[issue_number]`, replacing `[issue_number]` with your issue number, e.g. `feature-issue-1`.
3. Write the code, test it etc. Remember to use unit tests. Every time you reach a key 'checkpoint' in your feature development, complete **step 4**. *A 'commit' is like a checkpoint.*
4. Run `git commit -a -m "[commit_name]"` replacing `[commit_name]` with your commit name, e.g. `implemented Node class`, to link it to your assigned issue. If this tells you to add files, run `git add -A` and retry. 
5. When you have reached a key milestone of developing your feature, which may occur just once, run `git push origin feature-issue-[issue-number]`. *(Once you've used this command once on this branch and it shows up on the website, you only need to use `git push`).*
6. Go to the [repository pull requests on Github](https://github.com/CM20314/Android/pulls).
7. Click **New pull request**.
8. Set the `base` to `development`.
9. Set the `compare` to `feature-issue-[issue-number]`. You will now see a list of your commits for your feature.
10. Click **Create pull request**.
11. Add a title, e.g. ```Implemented database model classes```.
12. Add a description which mentions the issue number(s), e.g. ```Resolved #1``` or ```Resolved #1 and #2```.
13. I will then review your pull request, resolve any merge conflicts and let you know if there are any changes that need to be made by adding a comment to the request. If there are, then make the changes in your same feature branch and follow **steps 3 to 5** - once you push, this will update the pull request automatically. 
14. If you are sure you are done with the feature branch, run `git branch -D feature-issue-[issue-number]` to delete it. You won't need it any more. Otherwise, refer to step 3 of [Branch information](#branch-information).

~~5. Run `git checkout development`. This moves your `HEAD` to the `development` branch.~~
~~6. Run `git pull` (if this doesn't work, `git pull origin development`) to pull changes from the cloud to your local copy so as not to overwrite others' work.~~
~~7. Run `git merge feature-issue-[issue_number]`. This will merge your changes.~~
~~8. If someone else has made a change to the same file as you, there will be a merge conflict. To resolve this:~~
~~* Open the conflicted file and manually resolve the conflict, keeping the lines of code you want to keep.~~
~~* Once you've sorted them all out, run `git add -A` to add all of the files.~~
~~* Run `git merge --continue` to complete the merge.~~
~~9. Run `git commit -a -m "merged feature-[feature_name] into development"`.~~
~~10. If you aren't going to merge any more changes, update the remote copy by running `git push`.~~

## Branch information
1. Please be very careful when merging. Make sure you run `git pull` before you push local changes.
2. There are currently four branches:
* `development` is to develop the API. Create new branches for new features you are working on, then merge back to `development` when you are done.
* `alpha` is for alpha testing. This will (once I set it up) automatically build the app and release it to our Android devices using Microsoft AppCenter.
* `beta` is for beta testing. This will (once I set it up) automatically build the app and release it to our beta testers using Microsoft AppCenter.
* `master` is for production. Do not push to this branch before consulting the group.
3. To view your local branches, run `git branch`. To switch branch, run `git checkout [branch_name]`.
4. **Every merge should follow the order `development > alpha > beta > master`.**