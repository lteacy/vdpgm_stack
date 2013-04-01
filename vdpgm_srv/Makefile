PACKAGE=vdpgm_srv
PACKAGE_DIR=`rospack find $(PACKAGE)`
SOURCES=$(shell find $(PACKAGE_DIR)/src/main -name *.java)

all: installed

installed: $(SOURCES)
	`rosstack find rosjava_core`/gradlew installApp
	mkdir -p $(PACKAGE_DIR)/bin
	ln -sf $(PACKAGE_DIR)/build/install/$(PACKAGE)/bin/$(PACKAGE) bin/execute
	chmod a+x bin/execute
	touch installed rospack_nosubdirs

clean:
	`rosstack find rosjava_core`/gradlew clean
	rm -rf bin installed rospack_nosubdirs
