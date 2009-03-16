require "java"
include_class "org.kompiro.jamcircle.kanban.model.mock.Card"
include_class "org.kompiro.jamcircle.kanban.model.mock.Lane"
board = $bsf.lookupBean('board');
monitor = $bsf.lookupBean('monitor');
JRubyType = $bsf.lookupBean('JRubyType');
JavaScriptType = $bsf.lookupBean('JavaScriptType');