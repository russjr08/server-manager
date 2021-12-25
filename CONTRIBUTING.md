Contributor License Agreement (also known as the CLA)

**EFFECTIVE AS OF 12/25/21 04:00 AM GMT**
**LAST UPDATED AT 12/25/21 04:00 AM GMT**

Server Manager and any subprojects, if any, is completely Open Source (under the GPLv2 license) - as such, contributions are always welcome!

### Agreement clauses

However, in order to contribute, you must agree to the following conditions:

- You agree that by contributing to Server Manager, if your Merge Request is accepted, your contributed code will become a part of the Server Manager codebase, and as such will adopt the same license used by The Server Manager Project (GPLv2 - see the `LICENSE` file in the repository) - it will not be possible to remove your code from the codebase, nor can you revoke the license/copyright to use your code (or other contributions). 

  - However, at any time **before** your code is merged, you can request that the Merge Request be closed / not-merged.
    - The onus is on you to make this intent known as quickly as possible, before your contribution is merged, as once it has been merged there is no going back! 
  - This is also in part because of the fact that it's nearly impossible to remove a commit from history once it's been merged.

- You agree that you will follow the "Golden Rule", and treat any reviewer, or any other commenter of your Merge Request with respect.
  - Failure to abide by this will bar you from contributing further, this however will **not** allow you to revoke the copyright of your contribution(s), and you may be restricted from commenting in Merge Requests (or other RFCs).

- You agree that if you do not understand anything outlined in this agreement, that you will request clarification before signing this agreement.
  - By signing this agreement, you are signaling that you fully understand the terms outlined in this agreement.

- You must be of age in your jurisdication, to make legally binding agreements.
  - You should also be of legal age in the United States of America (18), as this is where 
  - By signing this agreement, you adjudicate that you meet this requirement.
  - There are no exceptions to this clause (unless **specifically** agreed on by a Project Lead), as if you cannot make a legally binding agreement, you cannot legally agree to these terms. 

- You agree to act in "good faith" to not break any existing features of The Server Manager project.
  - Exception: You may break/remove an existing feature, if it is being replaced by a better feature.
  - However, you **can** submit a Merge Request that isn't ready to ship - but, your Merge Request must be marked as a Draft 
    - Preferably by using the "Mark as Draft" feature in GitLab, or an equivalent if contributing to version control outside of GitLab. 
      - If no such feature exists, or you do not know how to access/utilize it, you should make a comment in the Merge Request noting that it is a draft. You will be responsible for noting  when it is "complete" however, as it will not be merged until then.
  - The Server Manager Project contains a CI script to ensure that the project can be compiled, you can replicate this locally by running `./gradlew build`
    - Your Merge Request should be marked as a Draft (see the above notes about this), as it will not be merged if the build fails.

- You agree to resign this Agreement if the terms are updated (see the `LAST UPDATED AT` notice at the top of the Agreement), if you previously signed the agreement before the `LAST UPDATED AT` time.
  - Failure to do so will result on your Merge Request not being merged, until you have done so.

- You are allowed to contribute to the Contributor License Agreement, so long as update the `LAST UPDATED AT` time at the top of this agreement AND you must resign the agreement with a timestamp at or after the new `LAST UPDATED AT` time.
  - However, you must act in "good faith" to make any additional/modified terms "reasonable" to future contributors.
    - "Reasonable" is at the discretion of a Project Lead.

### Project Leads

- Russell Richardson (`russjr08`) <admin@russ.network>

### How To Sign This Agreement

- To sign this agreement, you should add your name, email address, and timestamp (which must roughly match the commit time that contains your signature) to the `CONTRIBUTORS` list right after this section.
  - To keep consistentcy, you **must** use GMT as the timezone in your timestamp (please make sure it actually matches GMT time, don't just replace the timezone).
  - Your name must match the name that you use for your commits, so if you do not wish to use your real name that is fine, as long as the name in your commit matches the name you sign with.
    - The same applies to your email address, but it's preferred (however not strictly required) to use an email address that you can be reached at if needed.
  - If you switch the name or email address that you commit with, you must resign this agreement with your new details before any further Merge Requests will be merged.
  - Example: Russell Richardson \<admin@russ.network> - 12/25/21 04:00AM GMT
    - Use a `\\` to escape the `<>`'s in your email address, like so:
      - `\<admin@russ.network>`
- Then, you are required to sign-off your commit that contains your addition to the the `CONTRIBUTORS` list.
  - Use `git commit --signoff` to do this

### Resigning This Agreement
- To resign this Agreement, follow the same instructions as `How To Sign This Agreement` however do **NOT** edit your existing signature, rather ammend it at the end of the list.

## CONTRIBUTORS
Russell Richardson \<admin@russ.network> 12/25/21 04:05AM GMT
Russell Richardson \<Russell@KronosAD.com> 12/25/21 04:05AM GMT