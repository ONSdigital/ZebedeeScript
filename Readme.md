# ZebedeeScript

ZebedeeScript is a command line language aimed at taming the beast of content management in the ONS. It is an extension to the Flatsy command line language which was also designed for the ONS. Flatsy commands can be used to search and manipulate flat file json databases. 

ZebedeeScript also has some tricks of it's own. Using ZebedeeScript we can run commands on Zebedee without having to do things manually through Florence. 


## Zebedee commands

#### Log in to zebedee
```python
# Log in to zebedee - will require a password
login <username>

```

#### Collections & content
```python
# Create a new collection
collection create <collection name>

# Build a new collection from a folder of items
collection build <collection name> <full local directory name>

# Add a file to a collection
collection add <collection name> <uri> <full local filename>

# Complete all files in a collection
collection complete <collection name>

# Review all files in a collection
collection review <collection name>
```

#### Users
```python
# Add a new user
users add <email> <name>

# Set user password
users password <email> <password>

# Assign publisher permissions
users permissions publisher <email>

# Assign admin permissions
users permissions admin <email>

# Assign viewer permissions
users permissions viewer <email>
```

#### Teams
```python
# Add a team
teams add <team name>

# Add to a collection
collection teams add <team name>
```

## Database
Database commands work
- declare the root of your file system
- query the database using filter commands
- apply an operation

### Example
```python
# Find any invalid json files 

from /Users/thomasridd/onswebsite
filter uri_ends .json
filter not json valid
list
```

### Commands

#### Select database root
```python
# Pick a root directory to work from (often this will be /zebedee/master)
from <full file path>

```

#### Simple filters
```python
# Uri 
filter uri_contains <value>
filter uri_ends <value>

```

#### Logic
```python
# Not
filter not [any other args]

```

#### JSON
```python
# JsonPath variable
filter json <$.path> equals <value>

# JsonPath collection
filter json <$.collection> contains <value> 

# Json Valid
filter json valid
```


### Flat file database examples

```python
# Find any invalid json files 

from /Users/thomasridd/onswebsite
filter uri_ends .json
filter not json valid
list
```

```python
# A complete content list of all non-timeseries items

from /Users/thomasridd/onswebsite
filter uri_ends .json
filter not uri_contains /timeseries/
table $.description.title $.type ~.parent
```
