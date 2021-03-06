/*-
 * #%L
 * JGitFlow :: Maven Plugin
 * %%
 * Copyright (C) 2017 Atlassian Pty, LTD, Ultreia.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import com.atlassian.maven.plugins.jgitflow.it.FinishScriptHelper
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.CreateBranchCommand

helper = new FinishScriptHelper(basedir, localRepositoryPath, context)
    gits = helper.createAndCloneRepo("1.0","1.1-SNAPSHOT","1.0.1","hotfix/")

    localGit = gits.local;
    localGit.checkout().setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setCreateBranch(true).setStartPoint("origin/hotfix/1.0.1").setName("hotfix/1.0.1").call()

    File junkFile = new File(localGit.getRepository().getWorkTree(), "junk.txt");
    FileUtils.writeStringToFile(junkFile, "I am junk");
    localGit.add().addFilepattern(junkFile.getName()).call();
    localGit.commit().setMessage("adding junk file").call();

    return true;
