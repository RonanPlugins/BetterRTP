const mc = require('minecraft-protocol');
const md = require('minecraft-data')(process.argv[2]);
const chat = require('prismarine-chat')(process.argv[2]);
const client = mc.createClient({host:'127.0.0.1',port:25585,username:process.argv[3],version:process.argv[2],auth:'offline'});
const report={positions:0,sounds:0,particles:0,effects:0,success:0,delays:0,messages:[]};
let loaded=false;
client.on('position', p=>{
 report.positions++;
 client.write('teleport_confirm',{teleportId:p.teleportId});
 if (!loaded && md.protocol.play.toServer.types.packet_player_loaded) {client.write('player_loaded',{});loaded=true;}
 console.log(JSON.stringify({position:p}));
});
client.on('chunk_batch_finished',()=>client.write('chunk_batch_received',{chunksPerTick:10}));
client.on('systemChat', p=>{
 let s; try {s=chat.fromNotch(p.formattedMessage).toString();}catch {s=JSON.stringify(p);}
 report.messages.push(s);if(s.includes("You have been tp'd"))report.success++;if(s.includes('Teleporting in'))report.delays++;
 console.log(JSON.stringify({message:s}));
});
client.on('packet',(p,m)=>{
 if (['sound_effect','named_sound_effect'].includes(m.name) && JSON.stringify(p).includes('minecraft:entity.')){report.sounds++;console.log(JSON.stringify({sound:p}));}
 if(m.name==='world_particles')report.particles++;
 if(m.name==='entity_effect')report.effects++;
});
client.on('error',e=>console.log(JSON.stringify({error:e.message})));
client.on('end',r=>{console.log(JSON.stringify({end:r,report}));process.exit(report.success>=2&&report.positions>=3&&report.sounds>=3&&report.effects>=2&&report.particles>0&&report.delays===1?0:1);});
setTimeout(()=>client.end('Integration test complete'),45000);
