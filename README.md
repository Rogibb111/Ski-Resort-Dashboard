# Ski-Resort-Dashboard
The ski resort dashboard is a a small web app designed to display aggregated data about some of the local ski resorts that I like to frequent in Colorado. The idea is similar to what OnTheSnow does, but with a slightly more focused goal on displaying the historical weather data of each resort. The reason for this is to try and get a slighly better picture of what conditions will be like on the mountain. 

## Setup
This is the setup that I have been using and since I am new to Scala, this is the only one that I know. I have been using Visual Studio code along with Metals as my IDE. I have also been developing on a Windows machine so the instructions are tailored to setting up in those to enviornments.

### Installation Prerequisites
1. Install the Coursier tool from [here](https://git.io/coursier-cli-windows-exe)
2. Open a command prompt and run `cs setup`

4. Open folder in VSCode. Hit Ctrl-Shift-P and type **Install Extensions**
5. Search for and install **Scala Syntax** and **Scala (Metals)**

### Installation Procedure
1. Open a command prompt, pick a folder where you want this repository to live and run `git clone git@github.com:Rogibb111/Ski-Resort-Dashboard-Backend.git`
2. Open VSCode and hit Ctrl-k then Ctrl-o, navigate to the repo folder, and hit select folder.
3. Hit Ctrl-Shift-P and type **Metals: Import Build**

### Running The Application
1. Open a command prompt and navigate to the top level of the repo and run `sbt run`
2. Open a browser and navigate to **localhost:9000**