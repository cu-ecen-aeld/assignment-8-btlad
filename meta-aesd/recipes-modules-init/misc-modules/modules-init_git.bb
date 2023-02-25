# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
#
# The following license files were not able to be identified and are
# represented as "Unknown" below, you will need to check them yourself:
#   LICENSE

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
           file://module_load   \
           file://modules.init  \
           file://module_unload \
           file://scull_load    \
           file://scull_unload  \
          "

# Modify these as desired
PV = "1.0"

S = "${WORKDIR}"

# Add the scull and misc-modules init files need to install
FILES:${PN} += "${base_bindir}/module_load \
                ${base_bindir}/module_unload \
                ${base_bindir}/scull_load \
                ${base_bindir}/scull_unload \
                ${sysconfdir}/init.d/modules.init \
               "

inherit update-rc.d

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "modules.init"

do_compile () {
}

do_install () {
    install -d ${D}${base_bindir}
    install -m 0755 ${S}/module_load ${D}${base_bindir}/
    install -m 0755 ${S}/module_unload ${D}${base_bindir}/
    install -m 0755 ${S}/scull_load ${D}${base_bindir}/
    install -m 0755 ${S}/scull_unload ${D}${base_bindir}/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/modules.init ${D}${sysconfdir}/init.d
}
