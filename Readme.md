# ZebedeeScript

ZebedeeScript is a command line language aimed at taming the beast of content management in the ONS. It is an extension to the Flatsy command line language which was also designed for the ONS. Flatsy commands can be used to search and manipulate flat file json databases. 

ZebedeeScript also has some tricks of it's own. Using ZebedeeScript we can run commands on Zebedee without having to do things manually through Florence. 


### Zebedee commands

#### Log in to zebedee
```python
login <username>
```

#### Collections
```python
# Create a new collection
collection create <collection name>

# Create a new collection
collection build <collection name> <>
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
